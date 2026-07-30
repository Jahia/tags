/*
 * Copyright (C) 2002-2022 Jahia Solutions Group SA. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jahia.modules.tags;

import org.apache.commons.lang.StringUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Renders a settings component only when the caller holds an administration permission on the
 * resource the request is actually made against.
 * <p>
 * The tag-management screen is an ordinary, instantiable content type, so the settings container it was
 * designed for is not the only place it can be rendered from. The permission requirement is declared on the
 * settings <em>template</em> that hosts it ({@code j:requiredPermissionNames}) and is therefore a property of
 * that template: the same component rendered through any other resource — a plain page content area — carries
 * no requirement at all, and the web flow behind it would be handed to whoever can render that resource. This
 * filter makes the requirement a property of the component, so it holds on every render path.
 * <p>
 * The check is evaluated against the <strong>main resource</strong> of the render, not against the component
 * node, and for this screen that is essential rather than incidental. Its settings template applies on
 * {@code jnt:virtualsite} under {@code tagManager}, so this is a <em>site-scoped</em> screen and a site
 * administrator is its legitimate caller. That role is granted on {@code /sites/<key>}, while the component
 * node of the legitimate screen lives inside the hosting module ({@code /modules/...}), where they hold
 * nothing — measured, {@code site-admin} is {@code false} on the component node and {@code true} on the site.
 * Checking the component node, which is the obvious implementation, would therefore refuse real site
 * administrators; the main resource is the site itself.
 * <p>
 * {@code admin} is accepted alongside {@code site-admin} so a server administrator reaching the screen is not
 * refused either. Both are core permissions
 * ({@code root-permissions.xml}) granted by the {@code site-administrator} / {@code server-administrator}
 * roles; the finer per-screen requirement remains enforced on the administration route by the template, so
 * this filter is an additional condition and never a replacement. Failing to resolve a main resource, or a
 * missing configuration, yields an empty fragment rather than a rendered component.
 */
// equals/hashCode are deliberately NOT overridden for the field below: AbstractFilter defines equality as
// (concrete class, priority), which is the key RenderService.addFilter uses to replace an already-registered
// filter. Widening it to the configuration would break that re-registration.
@SuppressWarnings("java:S2160")
public class SettingsComponentPermissionFilter extends AbstractFilter {

    private static final Logger logger = LoggerFactory.getLogger(SettingsComponentPermissionFilter.class);

    private String[] requiredPermissions = new String[0];
    private String requiredPermissionsLabel = "";

    /**
     * Sets the permissions accepted by this filter, as a comma-separated list. A caller holding any one of
     * them on the main resource may render the component.
     *
     * @param requiredPermissions comma-separated list of permission names
     */
    public void setRequiredPermissions(String requiredPermissions) {
        String[] parsed = StringUtils.split(StringUtils.defaultString(requiredPermissions), ',');
        for (int i = 0; i < parsed.length; i++) {
            parsed[i] = parsed[i].trim();
        }
        this.requiredPermissions = parsed;
        this.requiredPermissionsLabel = StringUtils.join(parsed, ", ");
    }

    @Override
    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        if (requiredPermissions.length == 0) {
            logger.error("No permission configured for {}; refusing to render {}",
                    getClass().getName(), resource.getNodePath());
            return StringUtils.EMPTY;
        }

        Resource mainResource = renderContext.getMainResource();
        JCRNodeWrapper contextNode = mainResource != null ? mainResource.getNode() : null;
        if (contextNode == null) {
            logger.warn("No main resource to evaluate {} against; not rendering it", resource.getNodePath());
            return StringUtils.EMPTY;
        }

        for (String permission : requiredPermissions) {
            if (contextNode.hasPermission(permission)) {
                return null;
            }
        }

        if (logger.isWarnEnabled()) {
            logger.warn("Not rendering {}: {} holds none of {} on {}", resource.getNodePath(),
                    renderContext.getUser() != null ? renderContext.getUser().getName() : "the current user",
                    requiredPermissionsLabel, contextNode.getPath());
        }
        return StringUtils.EMPTY;
    }
}
