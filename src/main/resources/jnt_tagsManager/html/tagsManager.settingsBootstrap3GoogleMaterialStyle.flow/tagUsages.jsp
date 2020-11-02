<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="flowRequestContext" type="org.springframework.webflow.execution.RequestContext"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="tagDetails" type="org.jahia.modules.tags.webflow.model.TagUsages"--%>

<template:addResources type="css" resources="admin-font-awesome-v4.2.0.min.css"/>
<template:addResources type="css" resources="datatables/css/bootstrap-theme.css"/>
<template:addResources type="css" resources="typeahead.css"/>

<template:addResources type="javascript" resources="jquery.min.js"/>
<template:addResources type="javascript" resources="jquery-ui.min.js,jquery.blockUI.js,workInProgress.js"/>
<template:addResources type="javascript" resources="datatables/jquery.dataTables.min.js,datatables/dataTables.bootstrap-ext.js,i18n/jquery.dataTables-${currentResource.locale}.js,datatables/dataTables.i18n-sorting-ext.js,settings/dataTables.initializer.js"/>
<template:addResources type="javascript" resources="bootbox.min.js"/>
<template:addResources type="javascript" resources="underscore.min.js"/>
<template:addResources type="javascript" resources="typeahead.min.js"/>
<template:addResources type="javascript" resources="tagUsages.js"/>

<fmt:message key="label.cancel" var="labelCancel"/>
<fmt:message key="label.ok" var="labelOk"/>
<fmt:message key="label.rename" var="labelRename"/>
<fmt:message key="label.delete" var="labelDelete"/>
<fmt:message key="label.workInProgressTitle" var="i18nWaiting"/>
<fmt:message key="jnt_tagsManager.label.tagNewName" var="labelTagNewName"/>
<fmt:message key="jnt_tagsManager.modal.rename" var="modalRename"/>
<fmt:message key="jnt_tagsManager.modal.delete" var="modalDelete"/>

<template:addResources type="inlinejavascript">
    <script>
        var jsVarMap = {
            labelCancel: '${functions:escapeJavaScript(labelCancel)}',
            labelOk: '${functions:escapeJavaScript(labelOk)}',
            labelRename: '${functions:escapeJavaScript(labelRename)}',
            labelDelete: '${functions:escapeJavaScript(labelDelete)}',
            i18nWaiting: '${functions:escapeJavaScript(i18nWaiting)}',
            labelTagNewName: '${functions:escapeJavaScript(labelTagNewName)}',
            modalRename: '${functions:escapeJavaScript(modalRename)}',
            modalDelete: '${functions:escapeJavaScript(modalDelete)}'
        };

        var tagsSuggester = new Bloodhound({
            datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
            queryTokenizer: Bloodhound.tokenizers.whitespace,
            limit: 10,
            remote: {
                <c:choose>
                <c:when test="${flowHandler.workspace eq 'default'}">
                url: '${url.context}${url.baseEdit}${renderContext.siteInfo.sitePath}.matchingTags.do' + '?q=%QUERY',
                </c:when>
                <c:otherwise>
                url: '${url.context}${url.baseLive}${renderContext.siteInfo.sitePath}.matchingTags.do' + '?q=%QUERY',
                </c:otherwise>
                </c:choose>
                filter: function (list) {
                    return $.map(list.tags, function (tag) {
                        return {
                            "value": tag.name
                        };
                    });
                }
            }
        });

        $(document).ready(function () {
            var dtOptions = {"aoColumns": [ //disable search for col 2 and 3
                null,
                { "bSearchable": false },
                { "bSearchable": false }
            ]};
            dataTablesSettings.init('tableTagDetails', 100, [[1, 'asc']], true, null, dtOptions);
            tagsSuggester.initialize();

            document.getElementById("backToTagList").addEventListener("click", function() {
                backToTagManager();
            });

            attachRenameAndDeleteListeners();
        });
    </script>
</template:addResources>

<div class="page-header">
    <h2><fmt:message key="jnt_tagsManager.title.detailsForTag"><fmt:param value="${tagDetails.tag}"/><fmt:param value="${fn:length(tagDetails.usages)}"/><fmt:param value="${flowHandler.workspace}"/></fmt:message></h2>
</div>

<c:forEach items="${flowRequestContext.messageContext.allMessages}" var="message">
    <div class="alert <c:choose><c:when test="${message.severity eq 'ERROR'}">alert-danger</c:when><c:otherwise>alert-success</c:otherwise></c:choose>" role="alert">
        <button type="button" class="close" data-dismiss="alert">&times;</button>
        ${message.text}
    </div>
</c:forEach>

<div class="panel panel-default">
    <div class="panel-heading">
        <div class="btn-group">
            <button type="button" class="btn btn-default btn-primary btn-raised" id="backToTagList">
                <fmt:message key="jnt_tagsManager.button.backToTagsList"/>
            </button>
        </div>
    </div>

    <div class="panel-body">
        <div class="row">
            <div class="col-md-12">
                <table class="table table-bordered table-striped" id="tableTagDetails">
                    <thead>
                    <tr>
                        <th>
                            <fmt:message key="label.page"/>
                        </th>
                        <th width="50%">
                            <fmt:message key="label.path"/>
                        </th>
                        <th width="25%">
                            <fmt:message key="label.actions"/>
                        </th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${tagDetails.usages}" var="usage">
                        <tr>
                            <td>
                                <c:if test="${not empty usage.displayablePath}">
                                    <c:choose>
                                        <c:when test="${flowHandler.workspace eq 'default'}">
                                            <a href="<c:url value="${url.basePreview}${usage.displayablePath}.html"/>" title="<fmt:message key="label.pagesTab"/>" target="_blank">
                                                    ${usage.displayableName}
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="<c:url value="${url.baseLive}${usage.displayablePath}.html"/>" title="<fmt:message key="label.pagesTab"/>" target="_blank">
                                                    ${usage.displayableName}
                                            </a>
                                        </c:otherwise>
                                    </c:choose>
                                </c:if>
                            </td>
                            <td>
                                ${usage.taggedNodePath}
                            </td>
                            <td>
                                <button type="button" class="btn btn-default btn-fab btn-fab-xs renameTagButton"
                                        data-toggle="tooltip" data-container="body" data-sel-role="renameTag"
                                        data-title="<fmt:message key='label.rename'/>"
                                        id="rename_${functions:escapeJavaScript(usage.taggedNodeIdentifier)}">
                                    <i class="material-icons">edit</i>
                                </button>
                                <button type="button" class="btn btn-danger btn-fab btn-fab-xs deleteTagButton"
                                        data-toggle="tooltip" data-container="body" data-sel-role="deleteTag"
                                        data-title="<fmt:message key='label.delete'/>"
                                        id="delete_${functions:escapeJavaScript(usage.taggedNodeIdentifier)}">
                                    <i class="material-icons">delete</i>
                                </button>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    <div class="row hide">
        <form:form id="formTagManagement" action="${flowExecutionUrl}" method="post">
            <input type="hidden" id="eventInput" name="_eventId_">
            <input id="selectedTag" type="hidden" name="selectedTag" value="${fn:escapeXml(tagDetails.tag)}">
            <input type="hidden" id="nodeToUpdateId" name="nodeToUpdateId"/>
            <input type="hidden" id="tagNewName" name="tagNewName">
        </form:form>
    </div>
</div>
