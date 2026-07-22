import React, {useContext, useState} from 'react';
import PropTypes from 'prop-types';
import {shallowEqual, useSelector} from 'react-redux';
import useToggleWorkspace from '~/store/useToggleWorkspace';

export const TagsManagerContext = React.createContext({});
export const useTagsManagerContext = () => useContext(TagsManagerContext);

export const TagsManagerContextProvider = ({children}) => {
    const siteKey = useSelector(state => state.site, shallowEqual);
    const {workspace, otherWorkspace, toggleWorkspace} = useToggleWorkspace();
    const [filter, setFilter] = useState('');

    const context = {
        siteInfo: {siteKey, path: `/sites/${siteKey}`},
        search: {filter, setFilter},
        workspace, otherWorkspace, toggleWorkspace
    };

    return (
        <TagsManagerContext.Provider value={context}>
            {children}
        </TagsManagerContext.Provider>
    );
};

TagsManagerContextProvider.propTypes = {
    children: PropTypes.node.isRequired
};
