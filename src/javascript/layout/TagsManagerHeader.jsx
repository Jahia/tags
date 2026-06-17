import React from 'react';
import {Header, Separator} from '@jahia/moonstone';
import {useTagsManagerContext} from '~/store/TagsManager.context';
import {useTranslation} from 'react-i18next';
import RefreshButton from '~/layout/components/RefreshButton';
import clsx from 'clsx';
import TagSearchInput from '~/layout/components/TagSearchInput/TagSearchInput';
import SwitchWorkspaceButton from '~/layout/components/SwitchWorkspaceButton';

const TagsHeaderActions = () => (
    <div className={clsx('flexRow_nowrap', 'alignCenter')}>
        <TagSearchInput/>
        <Separator variant="vertical"/>
        <SwitchWorkspaceButton/>
    </div>
);

const TagsManagerHeader = () => {
    const {workspace} = useTagsManagerContext();
    const {t} = useTranslation('tags');

    return (
        <Header
            title={t('label.header.title', {workspace: workspace.displayName})}
            mainActions={<TagsHeaderActions/>}
            toolbarLeft={<RefreshButton/>}
        />
    );
};

export default TagsManagerHeader;
