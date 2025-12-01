import React from 'react';
import {IconTextIcon, Tag} from '@jahia/moonstone';
import PropTypes from 'prop-types';

export const NameCell = ({row}) => {
    return (
        <IconTextIcon iconStart={<Tag/>}>
            {row.values.name}
        </IconTextIcon>
    );
};

NameCell.propTypes = {
    row: PropTypes.object.isRequired
};
