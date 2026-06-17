import React from 'react';
import {DisplayActions} from '@jahia/ui-extender';
import {ButtonRendererNoLabel} from '@jahia/jcontent';
import PropTypes from 'prop-types';

export const ActionsCell = ({row}) => {
    return (
        <DisplayActions
            rowData={row?.original}
            target="tagsManagerTableActions"
            render={ButtonRendererNoLabel}
            buttonProps={{variant: 'ghost', size: 'big'}}/>
    );
};

ActionsCell.propTypes = {
    row: PropTypes.object.isRequired
};

