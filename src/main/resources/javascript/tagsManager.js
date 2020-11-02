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

function bbRenameTag(oldName) {
    bootbox.dialog({
        show: false,
        title: "<h4>" + jsVarMap.labelRename + " : " + oldName + "</h4>",
        message: "<p>" + jsVarMap.labelTagNewName +
                 "</p><input id='renameTag' class='typeahead' type='text' value='" + _.escape(oldName) + "' autofocus>" +
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
                 jsVarMap.modalRenameAll +
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
                    $("#eventInput").attr("name", "_eventId_renameAllTags");
                    $("#selectedTag").val(oldName);
                    $("#tagNewName").val($("#renameTag").val());
                    $("#formTagsManagement").submit();
                }
            }
        }
    })
        .off("shown.bs.modal")
        .on("shown.bs.modal", function () {
            $("#renameTag").focus();
        })
        .modal("show");
}

function bbDeleteTag(selectedTag) {
    bootbox.dialog({
        title: "<h4>" + jsVarMap.labelDelete + " : " + selectedTag + "</h4>",
        message: "<p>" + jsVarMap.modalDeleteAll + "</p>",
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
                    $("#eventInput").attr("name", "_eventId_deleteAllTags");
                    $("#selectedTag").val(selectedTag);
                    $("#formTagsManagement").submit();
                }
            }
        }
    });
}

function viewUsages(selectedTag) {
    callWorkInProgress();
    $("#eventInput").attr("name", "_eventId_viewTagUsages");
    $("#selectedTag").val(selectedTag);
    $("#formTagsManagement").submit();
}

function switchWorkspace() {
    callWorkInProgress();
    $("#eventInput").attr("name", "_eventId_switchWorkspace");
    $("#formTagsManagement").submit();
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
