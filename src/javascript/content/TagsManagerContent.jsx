import React from 'react';
import {Paper} from '@jahia/moonstone';
import TagsManagerTable from '~/content/TagsManagerTable';
import {useTagsManagerContext} from '~/store/TagsManager.context';
import {LoaderOverlay} from '@jahia/jahia-ui-root';
import useTagsData from '~/store/useTagsData';
const TagsManagerContent = () => {
    const {search} = useTagsManagerContext();
    const {data, loading} = useTagsData();

    let filteredData = data;
    if (search.filter) {
        filteredData = data.filter(row => {
            return row.name.value.toLowerCase().includes(search.filter.toLowerCase());
        });
    }

    return (
        <Paper>
            {loading ?
                <LoaderOverlay/> :
                <TagsManagerTable rowData={filteredData}/>}
        </Paper>
    );
};

export default TagsManagerContent;
