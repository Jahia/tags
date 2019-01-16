/*
 * ==========================================================================================
 * =                   JAHIA'S DUAL LICENSING - IMPORTANT INFORMATION                       =
 * ==========================================================================================
 *
 *                                 http://www.jahia.com
 *
 *     Copyright (C) 2002-2019 Jahia Solutions Group SA. All rights reserved.
 *
 *     THIS FILE IS AVAILABLE UNDER TWO DIFFERENT LICENSES:
 *     1/GPL OR 2/JSEL
 *
 *     1/ GPL
 *     ==================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE GPL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *     2/ JSEL - Commercial and Supported Versions of the program
 *     ===================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE JSEL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     Alternatively, commercial and supported versions of the program - also known as
 *     Enterprise Distributions - must be used in accordance with the terms and conditions
 *     contained in a separate written agreement between you and Jahia Solutions Group SA.
 *
 *     If you are unsure which license is appropriate for your use,
 *     please contact the sales department at sales@jahia.com.
 */
package org.jahia.modules.tags.webflow;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.util.Text;
import org.jahia.api.Constants;
import org.jahia.modules.tags.webflow.model.TagUsage;
import org.jahia.modules.tags.webflow.model.TagUsages;
import org.jahia.services.content.*;
import org.jahia.services.query.ScrollableQuery;
import org.jahia.services.query.ScrollableQueryCallback;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.filter.cache.ModuleCacheProvider;
import org.jahia.services.tags.TaggingService;
import org.jahia.utils.i18n.Messages;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import java.io.Serializable;
import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by dgaillard on 05/11/14.
 */
public class TagsFlowHandler implements Serializable {

    private static final long serialVersionUID = -3325519642397714386L;

    private static final Logger logger = getLogger(TagsFlowHandler.class);

    public static final List<String> workspaces = Arrays.asList(Constants.EDIT_WORKSPACE, Constants.LIVE_WORKSPACE);

    @Autowired
    private transient TaggingService taggingService;

    @Autowired
    private transient JCRSessionFactory sessionFactory;

    @Autowired
    private transient ModuleCacheProvider moduleCacheProvider;

    private String workspace;

    public void init() {
        workspace = Constants.EDIT_WORKSPACE;
    }

    public void switchWorkspace() {
        workspace = workspace.equals(Constants.EDIT_WORKSPACE) ? Constants.LIVE_WORKSPACE : Constants.EDIT_WORKSPACE;
    }

    public Map<String, Integer> getTagsList(RenderContext renderContext) {
        try {
            JCRSessionWrapper session = renderContext.getMainResource().getNode().getSession();
            final String sitePath = renderContext.getSite().getPath();
            return JCRTemplate.getInstance().doExecute(session.getUser(), workspace, session.getLocale(), new JCRCallback<Map<String, Integer>>() {
                @Override
                public Map<String, Integer> doInJCR(JCRSessionWrapper jcrSessionWrapper) throws RepositoryException {
                    String query = "SELECT * FROM [jmix:tagged] AS result WHERE ISDESCENDANTNODE(result, '" + JCRContentUtils.sqlEncode(sitePath) + "') AND (result.[j:tagList] IS NOT NULL)";
                    QueryManager qm = jcrSessionWrapper.getWorkspace().getQueryManager();
                    Query q = qm.createQuery(query, Query.JCR_SQL2);
                    ScrollableQuery scrollableQuery = new ScrollableQuery(500, q);

                    return scrollableQuery.execute(new ScrollableQueryCallback<Map<String, Integer>>() {
                        Map<String, Integer> result = new HashMap<String, Integer>();

                        @Override
                        public boolean scroll() throws RepositoryException {
                            NodeIterator nodeIterator = stepResult.getNodes();
                            while (nodeIterator.hasNext()) {
                                JCRNodeWrapper nodeWrapper = (JCRNodeWrapper) nodeIterator.next();
                                JCRValueWrapper[] tags = nodeWrapper.getProperty("j:tagList").getValues();
                                for (JCRValueWrapper tag : tags) {
                                    String tagValue = tag.getString();

                                    if (result.containsKey(tagValue)) {
                                        result.put(tagValue, result.get(tagValue) + 1);
                                    } else {
                                        result.put(tagValue, 1);
                                    }
                                }
                            }
                            return true;
                        }

                        @Override
                        protected Map<String, Integer> getResult() {
                            return result;
                        }
                    });
                }
            });
        } catch (RepositoryException e) {
            logger.error("getTags() cannot get Tags List");
            return new HashMap<String, Integer>();
        }
    }

    public void renameAllTags(RenderContext renderContext, MessageContext messageContext, String selectedTag, String tagNewName) {
        if (StringUtils.isNotEmpty(tagNewName)) {
            JCRObservationManager.setAllEventListenersDisabled(Boolean.TRUE);
            try {
                for (String workspace : workspaces) {
                    final JCRSessionWrapper session = getSystemSessionWorkspace(renderContext, workspace);
                    Map<String, Set<String>> errors = taggingService.renameTagUnderPath(renderContext.getSite().getJCRLocalPath(), session, selectedTag, tagNewName,
                            new TagManagerActionCallback(moduleCacheProvider, session));

                    for (Map.Entry<String, Set<String>> entry : errors.entrySet()) {
                        for (String path : entry.getValue()) {
                            messageContext.addMessage(new MessageBuilder().error().defaultText(Messages.getWithArgs("resources.JahiaTags", "jnt_tagsManager.error.rename", renderContext.getUILocale(), selectedTag, entry.getKey(), path)).build());
                        }
                    }
                }
            } catch (RepositoryException e){
                messageContext.addMessage(new MessageBuilder().error().defaultText(Messages.getWithArgs("resources.JahiaTags", "jnt_tagsManager.error.renameAll.serverIssue", renderContext.getUILocale(), selectedTag, e.getMessage())).build());
            } finally {
                JCRObservationManager.setAllEventListenersDisabled(Boolean.FALSE);
            }
        } else {
            messageContext.addMessage(new MessageBuilder().error().defaultText(Messages.get("resources.JahiaTags", "jnt_tagsManager.error.newNameEmpty", renderContext.getUILocale())).build());
        }
    }

    public void deleteAllTags(RenderContext renderContext, MessageContext messageContext, String selectedTag) {
        JCRObservationManager.setAllEventListenersDisabled(Boolean.TRUE);
        try {
            for (String workspace : workspaces) {
                final JCRSessionWrapper session = getSystemSessionWorkspace(renderContext, workspace);
                Map<String, Set<String>> errors = taggingService.deleteTagUnderPath(renderContext.getSite().getJCRLocalPath(), session, selectedTag,
                        new TagManagerActionCallback(moduleCacheProvider, session));

                for (Map.Entry<String, Set<String>> entry : errors.entrySet()) {
                    for (String path : entry.getValue()) {
                        messageContext.addMessage(new MessageBuilder().error().defaultText(Messages.getWithArgs("resources.JahiaTags", "jnt_tagsManager.error.delete", renderContext.getUILocale(), selectedTag, entry.getKey(), path)).build());
                    }
                }
            }
        } catch (RepositoryException e){
            messageContext.addMessage(new MessageBuilder().error().defaultText(Messages.getWithArgs("resources.JahiaTags", "jnt_tagsManager.error.deleteAll.serverIssue", renderContext.getUILocale(), selectedTag, e.getMessage())).build());
        } finally {
            JCRObservationManager.setAllEventListenersDisabled(Boolean.FALSE);
        }
    }

    public TagUsages getTagDetails(final RenderContext renderContext, final String selectedTag) {
        try {
            JCRSessionWrapper session = renderContext.getMainResource().getNode().getSession();
            final String sitePath = renderContext.getSite().getPath();
            return JCRTemplate.getInstance().doExecute(session.getUser(), workspace, session.getLocale(), new JCRCallback<TagUsages>() {
                @Override
                public TagUsages doInJCR(JCRSessionWrapper jcrSessionWrapper) throws RepositoryException {
                    TagUsages tagUsages = new TagUsages();
                    tagUsages.setTag(selectedTag);
                    String query = "SELECT * FROM [jmix:tagged] AS result WHERE ISDESCENDANTNODE(result, '" + sitePath + "') AND (result.[j:tagList] = '" + Text.escapeIllegalXpathSearchChars(selectedTag).replaceAll("'", "''") + "')";
                    QueryManager qm = jcrSessionWrapper.getWorkspace().getQueryManager();
                    Query q = qm.createQuery(query, Query.JCR_SQL2);

                    NodeIterator ni = q.execute().getNodes();
                    while (ni.hasNext()) {
                        JCRNodeWrapper node = (JCRNodeWrapper) ni.nextNode();
                        JCRNodeWrapper displayableNode = JCRContentUtils.findDisplayableNode(node, renderContext);
                        TagUsage tagUsage = new TagUsage();
                        tagUsage.setTaggedNodePath(node.getPath());
                        tagUsage.setTaggedNodeIdentifier(node.getIdentifier());
                        tagUsage.setDisplayablePath(displayableNode != null ? displayableNode.getPath() : null);
                        tagUsage.setDisplayableName(displayableNode != null ? displayableNode.getDisplayableName() : null);
                        tagUsages.getUsages().add(tagUsage);
                    }

                    return tagUsages;
                }
            });

        } catch (RepositoryException e) {
            logger.error("getTagDetails() cannot get tag '" + selectedTag + "' details");
            return new TagUsages();
        }
    }

    public void renameTagOnNode(RenderContext renderContext, MessageContext messageContext, String selectedTag, String tagNewName, String nodeID) {
        if (StringUtils.isNotEmpty(tagNewName)) {
            JCRObservationManager.setAllEventListenersDisabled(Boolean.TRUE);
            JCRNodeWrapper node = null;

            try{
                for (String workspace : workspaces) {
                    try {
                        node = getSystemSessionWorkspace(renderContext, workspace).getNodeByIdentifier(nodeID);
                        taggingService.renameTag(node, selectedTag, tagNewName);
                        node.getSession().save();
                        //Added as we had all event listeners previously disabled for tags.
                        moduleCacheProvider.invalidate(node.getPath(), true);
                    } catch (RepositoryException e) {
                        if (node != null) {
                            messageContext.addMessage(new MessageBuilder().error().defaultText(Messages.getWithArgs("resources.JahiaTags", "jnt_tagsManager.error.rename", renderContext.getUILocale(), selectedTag, workspace, node.getPath())).build());
                        }
                    }
                }
            } finally {
                JCRObservationManager.setAllEventListenersDisabled(Boolean.FALSE);
            }
        } else {
            messageContext.addMessage(new MessageBuilder().error().defaultText(Messages.get("resources.JahiaTags", "jnt_tagsManager.error.newNameEmpty", renderContext.getUILocale())).build());
        }
    }

    public void deleteTagOnNode(RenderContext renderContext, MessageContext messageContext, String selectedTag, String nodeID) {
        JCRObservationManager.setAllEventListenersDisabled(Boolean.TRUE);
        JCRNodeWrapper node = null;
        try {
            for (String workspace : workspaces) {
                try {
                    node = getSystemSessionWorkspace(renderContext, workspace).getNodeByIdentifier(nodeID);
                    taggingService.untag(node, selectedTag);
                    node.getSession().save();
                    //Added as we had all event listeners previously disabled for tags.
                    moduleCacheProvider.invalidate(node.getPath(), true);
                }catch (RepositoryException e) {
                    if (node != null) {
                        messageContext.addMessage(new MessageBuilder().error().defaultText(Messages.getWithArgs("resources.JahiaTags", "jnt_tagsManager.error.delete", renderContext.getUILocale(), selectedTag, workspace, node.getPath())).build());
                    }
                }
            }
        } finally {
            JCRObservationManager.setAllEventListenersDisabled(Boolean.FALSE);
        }
    }

    private JCRSessionWrapper getSystemSessionWorkspace(RenderContext renderContext, String selectedWorkspace) throws RepositoryException {
        return sessionFactory.getCurrentSystemSession(selectedWorkspace, renderContext.getMainResourceLocale(), renderContext.getFallbackLocale());
    }

    public String getWorkspace() {
        return workspace;
    }
}
