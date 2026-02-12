import React from 'react';
import PropTypes from 'prop-types';
import {DisplayAction} from '@jahia/ui-extender';
import {ButtonRenderer} from '@jahia/jcontent';

export const ViewUsageCell = ({row}) => {
    return (
        <DisplayAction
            rowData={row?.original}
            actionKey="tagUsages"
            render={ButtonRenderer}
        />
    );
};

ViewUsageCell.propTypes = {
    row: PropTypes.object.isRequired
};
