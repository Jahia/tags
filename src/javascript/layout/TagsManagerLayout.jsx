import React from 'react';
import {LayoutContent} from '@jahia/moonstone';
import TagsManagerHeader from '~/layout/TagsManagerHeader';
import TagsManagerContent from '~/content/TagsManagerContent';
import {TagsManagerContextProvider} from '~/store/TagsManager.context';
import {ErrorBoundary} from '@jahia/jahia-ui-root';

const TagsManagerLayout = () => {
    return (
        <ErrorBoundary>
            <TagsManagerContextProvider>
                <LayoutContent
                    header={<TagsManagerHeader/>}
                    content={<TagsManagerContent/>}/>
            </TagsManagerContextProvider>
        </ErrorBoundary>
    );
};

export default TagsManagerLayout;
