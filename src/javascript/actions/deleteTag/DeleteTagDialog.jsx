import React from 'react';
import {useTranslation} from 'react-i18next';
import {Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle} from '@material-ui/core';
import {Button} from '@jahia/moonstone';
import PropTypes from 'prop-types';

const DeleteTagDialog = ({rowData, isOpen, onClose, onExited}) => {
    const {t} = useTranslation('tags');
    const onAction = () => {
        onClose();
    };

    return (
        <Dialog fullWidth
                maxWidth="sm"
                open={isOpen}
                aria-labelledby="edit-tag-dialog"
                data-sel-role="edit-tag-dialog"
                onExited={onExited}
                onClose={onClose}
        >
            <DialogTitle>
                {t('label.dialogs.deleteTag.title', {tag: rowData.name.value})}
            </DialogTitle>
            <DialogContent>
                <DialogContentText>{t('label.dialogs.deleteTag.confirm')}</DialogContentText>
            </DialogContent>
            <DialogActions>
                <Button size="big"
                        data-sel-role="cancel-button"
                        label={t('label.ui.cancel')}
                        onClick={onClose}/>
                <Button size="big"
                        color="danger"
                        data-sel-role="delete-tag-button"
                        label={t('label.dialogs.deleteTag.actionButton')}
                        onClick={onAction}/>
            </DialogActions>
        </Dialog>
    );
};

DeleteTagDialog.propTypes = {
    rowData: {name: {value: PropTypes.string}},
    isOpen: PropTypes.bool,
    onClose: PropTypes.func.isRequired,
    onExited: PropTypes.func.isRequired
};

export default DeleteTagDialog;
