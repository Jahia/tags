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
 */package org.jahia.modules.tags;

import org.apache.commons.lang.StringUtils;
import org.jahia.services.content.JCRContentUtils;
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

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Renders the tag-management component from the settings template that declares its access rule, and only
 * for a caller who holds that rule on the resource the request is made against.
 * <p>
 * The access rule of the screen is data: the {@code jnt:contentTemplate} that hosts the component states it
 * in {@code j:requiredPermissionNames}, which for this module's screen is {@code tagManager}. This filter
 * reads the rule from there and applies it, so the template definitions remain the single place the
 * requirement is expressed and this filter has nothing to keep in step with them. That also keeps the
 * screen's own granularity: it is rendered for a caller holding {@code tagManager}, whether that permission
 * is granted directly or through the {@code site-admin} permission that aggregates it — this module's
 * {@code permissions.xml} registers {@code tagManager} as a child of {@code site-admin}, and aggregation
 * runs downwards only, so a role naming the child does not hold the parent.
 * <p>
 * Two conditions, both required:
 * <ol>
 *   <li>the component renders from a module's template definitions
 *       ({@code /modules/<module>/<version>/templates/...}), which is where a settings screen is defined;</li>
 *   <li>the caller holds every permission the nearest declaring template ancestor requires.</li>
 * </ol>
 * A component whose ancestors declare no requirement, and a render with no resolvable context resource,
 * yield an empty fragment.
 * <p>
 * The permissions are evaluated against the render's <strong>context resource</strong> — the main resource,
 * or the ajax resource for an ajax sub-render — and not against the component node. That is load-bearing
 * rather than incidental: the component node of a settings screen lives inside its module
 * ({@code /modules/...}), where a site-scoped administrator holds nothing, while the context resource is the
 * site the screen administers, which is what the role is granted on. Resolving it this way mirrors core's
 * own evaluation of {@code j:requiredPermissionNames} ({@code TemplatePermissionCheckFilter}), so the screen
 * reached through its administration route resolves identically here.
 * <p>
 * Because {@code WebflowAction} re-enters the render chain for each webflow POST, both conditions cover
 * every transition and not just the initial GET. In studio, template permissions stay core's business and
 * this filter applies the placement condition alone, matching how core evaluates
 * {@code j:requiredPermissionNames} there.
 * <p>
 * Registered via OSGi Declarative Services — see the wiring note above, and the
 * {@code <_dsannotations>*</_dsannotations>} instruction in this module's pom that this line's parent
 * requires for the component to register at all.
 */
@Component(service = RenderFilter.class, immediate = true)
public class SettingsComponentPermissionFilter extends AbstractFilter {

    private static final Logger logger = LoggerFactory.getLogger(SettingsComponentPermissionFilter.class);

    /** Node types gated by this filter. */
    private static final String APPLY_ON_NODE_TYPES = "jnt:tagsManager";

    /** Where a settings screen is defined: a module's template definitions. */
    private static final Pattern MODULE_TEMPLATE_PATH = Pattern.compile("^/modules/[^/]+/[^/]+/templates/.+");

    /** The mixin a template carries to state an access rule, and the property that holds it. */
    private static final String DECLARING_TYPE = "jmix:requiredPermissions";
    private static final String DECLARED_PERMISSIONS = "j:requiredPermissionNames";

    private static final String STUDIO_MODE = "studiomode";

    @Activate
    public void activate() {
        // Priority 21.5: immediately after core's own permission check (TemplatePermissionCheckFilter, 21) and
        // clear of the 22.x template band. AbstractFilter breaks a priority tie on the class name, so an exact
        // 22 would order this against core's templateNodeFilter (22.0) by an accident of package naming rather
        // than by intent; 21.5 states the intended slot instead of relying on that.
        // This runs inside the fragment cache's generation scope (live only, 16 / 16.5), which is safe because
        // that cache keys on the caller's ACL signature: an entry generated for an administrator is not served
        // to a caller who lacks the grant.
        setPriority(21.5f);
        setApplyOnNodeTypes(APPLY_ON_NODE_TYPES);
        setDescription("Renders the tag-management component from its settings template, for a caller holding "
                + "the permissions that template declares");
        logger.debug("SettingsComponentPermissionFilter active on {}", APPLY_ON_NODE_TYPES);
    }

    @Override
    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        JCRNodeWrapper node = resource.getNode();
        String nodePath = node.getPath();

        if (!MODULE_TEMPLATE_PATH.matcher(nodePath).matches()) {
            logger.warn("Not rendering {}: a settings component renders from a module's template definitions",
                    nodePath);
            return StringUtils.EMPTY;
        }

        if (STUDIO_MODE.equals(renderContext.getEditModeConfigName())) {
            return null;
        }

        List<String> declared = declaredPermissions(node);
        if (declared.isEmpty()) {
            logger.warn("Not rendering {}: no template ancestor declares {}", nodePath, DECLARED_PERMISSIONS);
            return StringUtils.EMPTY;
        }

        JCRNodeWrapper contextNode = contextNode(renderContext);
        if (contextNode == null) {
            logger.warn("No resource to evaluate {} against; not rendering it", nodePath);
            return StringUtils.EMPTY;
        }

        for (String permission : declared) {
            if (!contextNode.hasPermission(permission)) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Not rendering {}: {} does not hold {} on {}", nodePath,
                            renderContext.getUser() != null ? renderContext.getUser().getName() : "the current user",
                            permission, contextNode.getPath());
                }
                return StringUtils.EMPTY;
            }
        }

        return null;
    }

    /**
     * The permissions required by the nearest ancestor that declares an access rule, empty when none does.
     */
    private static List<String> declaredPermissions(JCRNodeWrapper node) throws RepositoryException {
        JCRNodeWrapper declaring = node.isNodeType(DECLARING_TYPE)
                ? node
                : JCRContentUtils.getParentOfType(node, DECLARING_TYPE);
        if (declaring == null || !declaring.hasProperty(DECLARED_PERMISSIONS)) {
            return Collections.emptyList();
        }
        List<String> permissions = new ArrayList<>();
        for (Value value : declaring.getProperty(DECLARED_PERMISSIONS).getValues()) {
            String permission = value.getString();
            if (StringUtils.isNotBlank(permission)) {
                permissions.add(permission);
            }
        }
        return permissions;
    }

    /**
     * The resource the access rule is evaluated against: the ajax resource of an ajax sub-render, otherwise
     * the main resource of the render.
     */
    private static JCRNodeWrapper contextNode(RenderContext renderContext) {
        Resource contextResource = renderContext.getAjaxResource() != null
                ? renderContext.getAjaxResource()
                : renderContext.getMainResource();
        return contextResource != null ? contextResource.getNode() : null;
    }
}
