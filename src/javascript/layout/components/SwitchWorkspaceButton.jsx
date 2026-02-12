import React from 'react';
import {useTagsManagerContext} from '~/store/TagsManager.context';
import {Button} from '@jahia/moonstone';
import {useTranslation} from 'react-i18next';

const SwitchWorkspaceButton = () => {
    const {otherWorkspace, toggleWorkspace} = useTagsManagerContext();
    const {t} = useTranslation('tags');

    const onSwitchWorkspace = e => {
        e.stopPropagation();
        toggleWorkspace();
    };

    return (
        <Button data-sel-role="switch-tag-workspace"
                variant="outlined"
                size="big"
                color="accent"
                label={t('label.header.actions.switchWorkspace', {otherWorkspace: otherWorkspace.displayName})}
                onClick={onSwitchWorkspace}
        />
    );
};

export default SwitchWorkspaceButton;
