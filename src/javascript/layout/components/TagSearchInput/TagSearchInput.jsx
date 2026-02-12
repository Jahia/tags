import React from 'react';
import {SearchInput} from '@jahia/moonstone';
import {useTagsManagerContext} from '~/store/TagsManager.context';
import styles from './TagSearchInput.scss';

const TagSearchInput = () => {
    const {search: {setFilter}} = useTagsManagerContext();

    const handleFilterChange = event => {
        setFilter(event.target.value);
    };

    return (
        <SearchInput
            data-sel-role="search-tagsmanager"
            variant="ghost"
            size="big"
            className={styles.searchInput}
            placeholder="Search and press Enter"
            onChange={handleFilterChange}/>
    );
};

export default TagSearchInput;
