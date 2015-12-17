/**
 * ==========================================================================================
 * =                   JAHIA'S DUAL LICENSING - IMPORTANT INFORMATION                       =
 * ==========================================================================================
 *
 *                                 http://www.jahia.com
 *
 *     Copyright (C) 2002-2016 Jahia Solutions Group SA. All rights reserved.
 *
 *     THIS FILE IS AVAILABLE UNDER TWO DIFFERENT LICENSES:
 *     1/GPL OR 2/JSEL
 *
 *     1/ GPL
 *     ==================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE GPL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     This program is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU General Public License
 *     as published by the Free Software Foundation; either version 2
 *     of the License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
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

import org.jahia.services.content.JCRContentUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.filter.cache.ModuleCacheProvider;
import org.jahia.services.tags.TagActionCallback;

import javax.jcr.RepositoryException;
import java.util.*;

/**
 * Callback used on bench tag actions like rename all occurrences of a given tag under a site, or delete all occurrences of a tag under a site...
 * @author kevan
 */
public class TagManagerActionCallback implements TagActionCallback<Map<String, Set<String>>> {
    private ModuleCacheProvider moduleCacheProvider;
    private int counter;
    private JCRSessionWrapper session;
    Map<String, Set<String>> errors = new HashMap<>();

    public TagManagerActionCallback(ModuleCacheProvider moduleCacheProvider, JCRSessionWrapper session) {
        this.moduleCacheProvider = moduleCacheProvider;
        this.session = session;
        counter = 0;
    }

    @Override
    public void afterTagAction(JCRNodeWrapper node) throws RepositoryException {
        //save each 100 nodes processed
        counter ++;
        if(counter % 100 == 0){
            session.save();
        }

        // flush cache for node
        moduleCacheProvider.invalidate(node.getPath(), true);
        List<String> keys = moduleCacheProvider.getRegexpDependenciesCache().getKeys();
        for (String key : keys) {
            if (node.getPath().matches(key)) {
                moduleCacheProvider.invalidateRegexp(key, true);
            }
        }
    }

    @Override
    public void onError(JCRNodeWrapper node, RepositoryException e) {
        String displayableName = JCRContentUtils.getParentOfType(node, "jnt:page").getDisplayableName();
        if (!errors.containsKey(displayableName)) {
            errors.put(displayableName, new HashSet<String>());
        }
        errors.get(displayableName).add(node.getPath());
    }

    @Override
    public Map<String, Set<String>> end() throws RepositoryException{
        //do a final save
        session.save();

        // return errors
        return errors;
    }
}
