import React, {useEffect, useState} from 'react';
import {useTranslation} from 'react-i18next';
import {
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle,
    TextField
} from '@material-ui/core';
import {Button} from '@jahia/moonstone';
import PropTypes from 'prop-types';

const EditTagDialog = ({rowData, isOpen, onClose, onExited}) => {
    const {t} = useTranslation('tags');
    const [name, updateName] = useState('');
    const onAction = () => {
        onClose();
    };

    // Taken from jcontent RenameDialog

    const previousName = rowData.name.value;
    useEffect(() => {
        if (previousName) {
            updateName(previousName);
        }
    }, [previousName]);

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
                {t('label.dialogs.editTag.title', {tag: rowData.name.value})}
            </DialogTitle>
            <DialogContent>
                <DialogContentText>{t('label.dialogs.editTag.newName')}</DialogContentText>
                <TextField
                    fullWidth
                    autoFocus
                    value={name}
                    data-sel-role="edit-new-tag-name"
                    onChange={e => updateName(e.target.value)}
                />
                <DialogContentText>{t('label.dialogs.editTag.confirm')}</DialogContentText>
            </DialogContent>
            <DialogActions>
                <Button size="big"
                        data-sel-role="cancel-button"
                        label={t('label.ui.cancel')}
                        onClick={onClose}/>
                <Button size="big"
                        color="accent"
                        data-sel-role="action-button"
                        label={t('label.dialogs.editTag.actionButton')}
                        onClick={onAction}/>
            </DialogActions>
        </Dialog>
    );
};

EditTagDialog.propTypes = {
    rowData: {name: {value: PropTypes.string}},
    isOpen: PropTypes.bool,
    onClose: PropTypes.func.isRequired,
    onExited: PropTypes.func.isRequired
};

export default EditTagDialog;
