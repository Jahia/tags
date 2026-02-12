import {useState} from 'react';

const DEFAULT = {displayName: 'default', gqlType: 'EDIT'};
const LIVE = {displayName: 'live', gqlType: 'LIVE'};

const useToggleWorkspace = () => {
    const [workspace, setWorkspace] = useState(DEFAULT);
    const otherWorkspace = (workspace.gqlType === 'EDIT') ? LIVE : DEFAULT;
    const toggleWorkspace = () => {
        setWorkspace(otherWorkspace);
    };

    return {workspace, otherWorkspace, toggleWorkspace};
};

export default useToggleWorkspace;
