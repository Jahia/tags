window.jahia.i18n.loadNamespaces('tags');

window.jahia.uiExtender.registry.add('adminRoute', 'tagsmanager', {
    targets: ['jcontent:50'],
    label: 'tags:label.title',
    icon: window.jahia.moonstone.toIconComponent('CollectionsBookmark'),
    isSelectable: true,
    requiredPermission: 'tagManager',
    requireModuleInstalledOnSite: 'tags',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/$lang/sites/$site-key.tagsManager.html'
});
