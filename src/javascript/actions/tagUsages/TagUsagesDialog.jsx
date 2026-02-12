import React from 'react';
import {useTranslation} from 'react-i18next';
import {Dialog, DialogActions, DialogContent, DialogTitle} from '@material-ui/core';
import {Button} from '@jahia/moonstone';
import TagUsagesTable from '~/actions/tagUsages/TagUsagesTable';
import PropTypes from 'prop-types';

const TagUsagesDialog = ({rowData, isOpen, onClose, onExited}) => {
    const {t} = useTranslation('tags');

    return (
        <Dialog fullWidth
                maxWidth="lg"
                open={isOpen}
                aria-labelledby="tag-usages-dialog"
                data-sel-role="tag-usages-dialog"
                onExited={onExited}
                onClose={onClose}
        >
            <DialogTitle>
                {t('label.dialogs.tagUsages.title', {tag: rowData.name.value})}
            </DialogTitle>
            <DialogContent>
                <TagUsagesTable rowData={rowData?.usages || []}/>
            </DialogContent>
            <DialogActions>
                <Button size="big"
                        data-sel-role="close"
                        label={t('label.ui.close')}
                        onClick={onClose}
                />
            </DialogActions>
        </Dialog>
    );
};

TagUsagesDialog.propTypes = {
    rowData: {
        name: {value: PropTypes.string},
        usages: PropTypes.array
    },
    isOpen: PropTypes.bool,
    onClose: PropTypes.func.isRequired,
    onExited: PropTypes.func.isRequired
};

export default TagUsagesDialog;
