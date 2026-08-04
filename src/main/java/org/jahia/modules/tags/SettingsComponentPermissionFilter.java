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
/*
 * Wiring note — this is deliberately OSGi Declarative Services, not a Spring bean.
 *
 * Jahia's engineering conventions (the shared `cortex` harness, skill
 * `jahia-java-osgi-declarative-services`) state the rule as: DS is the ONLY dependency-injection
 * mechanism allowed in a Jahia module — Blueprint is deprecated and Spring is forbidden, with the sole
 * tolerated exception being a guarded `SpringContextSingleton.getBean(...)` read-through to a core bean.
 * An earlier revision of this filter was registered as a Spring bean in META-INF/spring; it was moved
 * here to follow that rule, and the render filter it registers behaves identically either way (both end
 * up in JahiaTemplateManagerService.getRenderFilters()).
 *
 * The same harness documents the trap to watch for if this is ever ported to another module: without the
 * bnd instruction `<_dsannotations>*</_dsannotations>`, an @Component class compiles and ships but emits
 * no OSGI-INF descriptor and no Service-Component header, so the component silently never registers and
 * the gate below simply does not run. Parents `jahia-modules` >= 8.1.7.0 switch it on already; older ones
 * do not. Verify a descriptor is actually in the built jar rather than assuming.
 */
package org.jahia.modules.tags;

import org.apache.commons.lang.StringUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.jahia.services.render.filter.RenderFilter;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Renders a settings component only when the caller holds an administration permission on the resource the
 * request is actually made against.
 * <p>
 * The permission requirement belongs on the component, not only on the settings template that normally
 * hosts it ({@code j:requiredPermissionNames}): a component's access rule should travel with the component
 * and hold on every render path, regardless of where the component is placed. This filter makes the
 * requirement a property of the component so it applies uniformly. Because {@code WebflowAction} re-enters
 * the render chain for each webflow POST, it covers every transition too, not just the initial GET.
 * <p>
 * The check is evaluated against the <strong>main resource</strong> of the render, not against the component
 * node, and that is load-bearing rather than incidental: the component node of a <em>legitimate</em> settings
 * screen lives inside its module ({@code /modules/...}), where a site-scoped administrator holds nothing —
 * measured, {@code site-admin} is {@code false} there and {@code true} on {@code /sites/<key>}. Checking
 * the component node, which is the obvious implementation, would therefore refuse real site administrators.
 * The main resource is the site (site-settings route) or the global settings node (server-administration
 * route), which is what the corresponding administrator role is actually granted on.
 * <p>
 * The screen is site-scoped ({@code j:applyOn=jnt:virtualsite}), so a site administrator is its legitimate
 * caller and {@code site-admin} must be accepted; {@code admin} is accepted alongside it so a server
 * administrator is not refused either. Both are core permissions ({@code root-permissions.xml}).
 * The finer per-screen requirement declared on the settings template remains enforced on the administration
 * route, so this filter is an additional condition and never a replacement. Failing to resolve a main resource
 * yields an empty fragment rather than a rendered component.
 * <p>
 * Registered via OSGi Declarative Services — no Spring context involvement.
 */
@Component(service = RenderFilter.class, immediate = true)
public class SettingsComponentPermissionFilter extends AbstractFilter {

    private static final Logger logger = LoggerFactory.getLogger(SettingsComponentPermissionFilter.class);

    /** Node types gated by this filter. */
    private static final String APPLY_ON_NODE_TYPES = "jnt:tagsManager";

    /** Any one of these on the main resource is sufficient. */
    private static final List<String> REQUIRED_PERMISSIONS =
            Collections.unmodifiableList(Arrays.asList("site-admin", "admin"));

    private static final String REQUIRED_PERMISSIONS_LABEL = StringUtils.join(REQUIRED_PERMISSIONS, ", ");

    @Activate
    public void activate() {
        // Priority 22: immediately after core's own permission check (TemplatePermissionCheckFilter, 21) and
        // before the fragment is produced or cached — a refusal must not populate a cache entry that a
        // differently-privileged caller could later be served.
        setPriority(22);
        setApplyOnNodeTypes(APPLY_ON_NODE_TYPES);
        setDescription("Renders a settings component only for a caller holding an administration permission "
                + "on the main resource");
        logger.debug("SettingsComponentPermissionFilter active on {}", APPLY_ON_NODE_TYPES);
    }

    @Override
    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        Resource mainResource = renderContext.getMainResource();
        JCRNodeWrapper contextNode = mainResource != null ? mainResource.getNode() : null;
        if (contextNode == null) {
            // Fail closed: with no main resource there is nothing to evaluate the permission against, and this
            // is an administration capability.
            logger.warn("No main resource to evaluate {} against; not rendering it", resource.getNodePath());
            return StringUtils.EMPTY;
        }

        for (String permission : REQUIRED_PERMISSIONS) {
            if (contextNode.hasPermission(permission)) {
                return null;
            }
        }

        if (logger.isWarnEnabled()) {
            logger.warn("Not rendering {}: {} holds none of {} on {}", resource.getNodePath(),
                    renderContext.getUser() != null ? renderContext.getUser().getName() : "the current user",
                    REQUIRED_PERMISSIONS_LABEL, contextNode.getPath());
        }
        return StringUtils.EMPTY;
    }
}
