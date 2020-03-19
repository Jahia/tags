window.jahia.i18n.loadNamespaces('tags');

window.jahia.uiExtender.registry.add('adminRoute', 'tagsmanager', {
    targets: ['jcontent:20'],
    label: 'tags:label.title',
    icon: null,
    isSelectable: true,
    requireModuleInstalledOnSite: 'tags',
    iframeUrl: window.contextJsParameters.contextPath + '/cms/editframe/default/en/sites/$site-key.tagsManager.html'
});
