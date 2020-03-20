window.jahia.i18n.loadNamespaces('tags');

window.jahia.uiExtender.registry.add('adminRoute', 'tagsmanager', {
    targets: ['jcontent:20'],
    label: 'tags:label.title',
    icon: null,
    isSelectable: true,
    requiredPermission: 'tagManager',
    requireModuleInstalledOnSite: 'tags',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/$lang/sites/$site-key.tagsManager.html'
});
