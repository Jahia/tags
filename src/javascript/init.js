import {registry} from '@jahia/ui-extender';
import i18next from 'i18next';
import {toIconComponent} from '@jahia/moonstone';

function registerRoutes() {
    registry.add('adminRoute', 'tags', {
        targets: ['jcontent:50'],
        label: 'tags:label.title',
        icon: toIconComponent('CollectionsBookmark'),
        isSelectable: true,
        requiredPermission: 'tagManager',
        requireModuleInstalledOnSite: 'tags',
        iframeUrl: `${window.contextJsParameters.contextPath}/cms/editframe/default/$lang/sites/$site-key.tagsManager.html`
    });
    console.log('%c Tags manager module registered routes', 'color: #3c8cba');
}

export default function () {
    registry.add('callback', 'tags', {
        targets: ['jahiaApp-init:23'],
        callback: async () => {
            await i18next.loadNamespaces('tags');
            registerRoutes();
        }
    });
}
