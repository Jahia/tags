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

<template:addResources type="css" resources="admin-font-awesome-v4.2.0.min.css"/>
<template:addResources type="css" resources="datatables/css/bootstrap-theme.css"/>
<template:addResources type="css" resources="typeahead.css"/>

<template:addResources type="javascript" resources="jquery.min.js"/>
<template:addResources type="javascript" resources="admin-bootstrap.js"/>
<template:addResources type="javascript" resources="jquery-ui.min.js,jquery.blockUI.js,workInProgress.js"/>
<template:addResources type="javascript" resources="datatables/jquery.dataTables.min.js,datatables/dataTables.bootstrap-ext.js,i18n/jquery.dataTables-${currentResource.locale}.js,datatables/dataTables.i18n-sorting-ext.js"/>
<template:addResources type="javascript" resources="bootbox.min.js"/>
<template:addResources type="javascript" resources="underscore.min.js"/>
<template:addResources type="javascript" resources="typeahead.min.js"/>
<template:addResources type="javascript" resources="tagsManager.js"/>

<fmt:message key="label.cancel" var="labelCancel"/>
<fmt:message key="label.ok" var="labelOk"/>
<fmt:message key="label.rename" var="labelRename"/>
<fmt:message key="label.delete" var="labelDelete"/>
<fmt:message key="label.workInProgressTitle" var="i18nWaiting"/>
<fmt:message key="jnt_tagsManager.label.tagNewName" var="labelTagNewName"/>
<fmt:message key="jnt_tagsManager.modal.renameAll" var="modalRenameAll"/>
<fmt:message key="jnt_tagsManager.modal.deleteAll" var="modalDeleteAll"/>

<script type="text/javascript">
    var jsVarMap = {
        labelCancel: '${functions:escapeJavaScript(labelCancel)}',
        labelOk: '${functions:escapeJavaScript(labelOk)}',
        labelRename: '${functions:escapeJavaScript(labelRename)}',
        labelDelete: '${functions:escapeJavaScript(labelDelete)}',
        i18nWaiting: '${functions:escapeJavaScript(i18nWaiting)}',
        labelTagNewName: '${functions:escapeJavaScript(labelTagNewName)}',
        modalRenameAll: '${functions:escapeJavaScript(modalRenameAll)}',
        modalDeleteAll: '${functions:escapeJavaScript(modalDeleteAll)}'
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
        $(document).ready(function () {
            $('#tableTagsList').dataTable({
                "sDom": "<'row-fluid'<'span6'l><'span6'f>r>t<'row-fluid'<'span6'i><'span6'p>>",
                "iDisplayLength": 100,
                "sPaginationType": "bootstrap",
                "aoColumns": [ //accent sorting for col 1, disable search for col 2 and 3
                    { targets: 0, type: 'diacritics-neutralise' },
                    { "bSearchable": false },
                    { "bSearchable": false }
                ]
            });
        });

        tagsSuggester.initialize();
        attachWorkspaceSwitch();
        attachRenameAndDeleteListeners();
        attachViewUsagesListeners();
    });
</script>
<div class="row-fluid">
    <div class="span6">
        <h3><fmt:message key="jnt_tagsManager.title.ListOgTags"><fmt:param value="${flowHandler.workspace}"/></fmt:message></h3>
    </div>
    <div class="span6">
        <button type="button" class="btn btn-primary pull-right" id="wsSwitch">
            <c:choose>
                <c:when test="${flowHandler.workspace eq 'default'}">
                    <fmt:message key="jnt_tagsManager.button.switchToDefault"/>&nbsp;<i class="fa fa-external-link"></i>
                </c:when>
                <c:otherwise>
                    <fmt:message key="jnt_tagsManager.button.switchToLive"/>&nbsp;<i class="fa fa-external-link"></i>
                </c:otherwise>
            </c:choose>
        </button>
    </div>
</div>
<div class="box-1">
    <div class="row-fluid">
        <div class="span12">
            <c:forEach items="${flowRequestContext.messageContext.allMessages}" var="message">
                <div class="alert <c:choose><c:when test="${message.severity eq 'ERROR'}">alert-error</c:when><c:otherwise>alert-success</c:otherwise></c:choose>">
                    <button type="button" class="close" data-dismiss="alert">
                        &times;
                    </button>
                    <c:choose>
                        <c:when test="${message.severity eq 'ERROR'}">
                            <i class="fa fa-exclamation"></i>&nbsp;<strong><fmt:message key="label.error"/></strong>&nbsp;
                        </c:when>
                        <c:otherwise>
                            <i class="fa fa-info"></i>&nbsp;
                        </c:otherwise>
                    </c:choose>
                    ${message.text}
                </div>
            </c:forEach>
        </div>
    </div>
    <div class="row-fluid">
        <div class="span12">
            <table class="table table-bordered table-striped table-hover" id="tableTagsList">
                <thead>
                <tr>
                    <th>
                        <fmt:message key="jnt_tagsManager.label.tag"/>
                    </th>
                    <th width="15%">
                        <fmt:message key="jnt_tagsManager.label.occurrences"/>
                    </th>
                    <th width="25%">
                        <fmt:message key="label.actions"/>
                    </th>
                </tr>
                </thead>
                <tbody>
                <c:set scope="page" var="totalTags" value="0"/>
                <c:forEach items="${tagsList}" var="tag" varStatus="tagsListIndex">
                    <c:set var="totalTags" value="${totalTags+tag.value}"/>
                    <tr>
                        <td>
                            ${tag.key}
                        </td>
                        <td>
                            ${tag.value}
                        </td>
                        <td>
                            <div class="btn-group pull-right">
                                <button type="button" class="btn btn-primary viewUsageButton" id="usage_${fn:escapeXml(functions:escapeJavaScript(tag.key))}">
                                    <i class="fa fa-search"></i>&nbsp;<fmt:message key="jnt_tagsManager.label.viewUsages"/>
                                </button>
                                <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
                                    <i class="fa fa-caret-down"></i>
                                </button>
                                <ul class="dropdown-menu">
                                    <li>
                                        <a href="#" class="renameTagButton" id="rename_${fn:escapeXml(functions:escapeJavaScript(tag.key))}">
                                            <i class="fa fa-pencil"></i>&nbsp;<fmt:message key="label.rename"/>
                                        </a>
                                    </li>
                                    <li>
                                        <a href="#" class="text-danger deleteTagButton" id="delete_${fn:escapeXml(functions:escapeJavaScript(tag.key))}">
                                            <i class="fa fa-trash"></i>&nbsp;<fmt:message key="label.delete"/>
                                        </a>
                                    </li>
                                </ul>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
<div class="row-fluid hide">
    <form:form id="formTagsManagement" action="${flowExecutionUrl}" method="post">
        <input type="hidden" id="eventInput" name="_eventId_">
        <input type="hidden" id="selectedTag" name="selectedTag">
        <input type="hidden" id="tagNewName" name="tagNewName">
    </form:form>
</div>

