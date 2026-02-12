import React from 'react';
import {Button, Loading} from '@jahia/moonstone';

const RefreshButton = () => {
    return (
        <Button
            data-sel-role="refresh-tags"
            variant="ghost"
            icon={<Loading/>}
            label="Refresh"/>
    );
};

export default RefreshButton;
