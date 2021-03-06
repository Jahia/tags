function callWorkInProgress(){
    if($.browser.msie == true){
        $.blockUI({ css: {
            border: 'none',
            padding: '15px',
            backgroundColor: '#000',
            '-webkit-border-radius': '10px',
            '-moz-border-radius': '10px',
            opacity: .5,
            color: '#fff'
        }, message: jsVarMap.i18nWaiting });
    } else {
        workInProgress(jsVarMap.i18nWaiting);
    }
}

function backToTagManager() {
    callWorkInProgress();
    $("#eventInput").attr("name", "_eventId_backToTagsList");
    $("#formTagManagement").submit();
}

function bbRenameTag(nodeID) {
    bootbox.dialog({
        title: "<h4>" + jsVarMap.labelRename + " : " + $('#selectedTag').val() + "</h4>",
        message: "<p>" + jsVarMap.labelTagNewName +
            "</p><input id='renameTag' class='typeahead' type='text' value='" + _.escape($('#selectedTag').val()) + "'>" +
            "<script>" +
                "$('.modal-body').css('overflow', 'visible');" +
                "$('#renameTag').keyup(function() {" +
                    "if ($('#renameTag').val() != '') {" +
                        "$('.renameButton').removeAttr('disabled');" +
                    "} else {" +
                        "$('.renameButton').attr('disabled', 'disabled');" +
                    "}" +
                "});" +
                "$('#renameTag').typeahead(null, {" +
                    "source: tagsSuggester.ttAdapter()" +
                "});" +
            "</script>" +
            "<br /><br /><p>" +
            jsVarMap.modalRename +
            "</p>",
        buttons: {
            danger: {
                label: jsVarMap.labelCancel,
                className: "btn-default",
                callback: function() {}
            },
            success: {
                label: jsVarMap.labelRename,
                className: "btn-primary btn-raised renameButton",
                callback: function() {
                    callWorkInProgress();
                    $("#eventInput").attr("name", "_eventId_renameTagOnNode");
                    $("#nodeToUpdateId").val(nodeID);
                    $("#tagNewName").val($("#renameTag").val());
                    $("#formTagManagement").submit();
                }
            }
        }
    });
}

function bbDeleteTag(nodeID) {
    bootbox.dialog({
        title: "<h4>" + jsVarMap.labelDelete + " : " + $('#selectedTag').val() + "</h4>",
        message: "<p>" + jsVarMap.modalDelete + "</p>",
        buttons: {
            danger: {
                label: jsVarMap.labelCancel,
                className: "btn-default",
                callback: function() {}
            },
            success: {
                label: jsVarMap.labelDelete,
                className: "btn-danger btn-raised",
                callback: function() {
                    callWorkInProgress();
                    $("#eventInput").attr("name", "_eventId_deleteTagOnNode");
                    $("#nodeToUpdateId").val(nodeID);
                    $("#formTagManagement").submit();
                }
            }
        }
    });
}

function attachRenameAndDeleteListeners() {
    var renameButtons = document.getElementsByClassName("renameTagButton");
    for (var i = 0; i < renameButtons.length; i++) {
        renameButtons[i].addEventListener("click", function(e) {
            bbRenameTag(e.currentTarget.id.replace("rename_", ""));
        });
    }

    var deleteButtons = document.getElementsByClassName("deleteTagButton");
    for (var i = 0; i < deleteButtons.length; i++) {
        deleteButtons[i].addEventListener("click", function(e) {
            bbDeleteTag(e.currentTarget.id.replace("delete_", ""));
        });
    }
}

function attachViewUsagesListeners() {
    var usageButtons = document.getElementsByClassName("viewUsageButton");
    for (var i = 0; i < usageButtons.length; i++) {
        usageButtons[i].addEventListener("click", function(e) {
            viewUsages(e.currentTarget.id.replace("usage_", ""));
        });
    }
}

function attachWorkspaceSwitch() {
    document.getElementById("wsSwitch").addEventListener("click", function () {
        switchWorkspace();
    })
}
