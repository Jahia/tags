import React, {useContext} from 'react';
import {Edit} from '@jahia/moonstone';
import {ComponentRendererContext} from '@jahia/ui-extender';
import {ButtonRendererNoLabel} from '@jahia/jcontent';
import EditTagDialog from './EditTagDialog';
import PropTypes from 'prop-types';

const EditTagAction = ({rowData, ...others}) => {
    const componentRenderer = useContext(ComponentRendererContext);
    return (
        <ButtonRendererNoLabel
            {...others}
            buttonIcon={<Edit/>}
            onClick={() => {
                componentRenderer.render('editTagDialog', EditTagDialog, {
                        rowData,
                        isOpen: true,
                        onClose: () => {
                            componentRenderer.setProperties('editTagDialog', {isOpen: false});
                        },
                        onExited: () => {
                            componentRenderer.destroy('editTagDialog');
                        }
                    }
                );
            }}
        />
    );
};

EditTagAction.propTypes = {
    rowData: {name: {value: PropTypes.string}}
};

export default EditTagAction;
