import React, {useContext} from 'react';
import {Delete} from '@jahia/moonstone';
import {ComponentRendererContext} from '@jahia/ui-extender';
import {ButtonRendererNoLabel} from '@jahia/jcontent';
import DeleteTagDialog from './DeleteTagDialog';
import PropTypes from 'prop-types';

const DeleteTagAction = ({rowData, ...others}) => {
    const componentRenderer = useContext(ComponentRendererContext);
    return (
        <ButtonRendererNoLabel
            {...others}
            buttonIcon={<Delete/>}
            onClick={() => {
                componentRenderer.render('deleteTagDialog', DeleteTagDialog, {
                    rowData,
                    isOpen: true,
                    onClose: () => {
                        componentRenderer.setProperties('deleteTagDialog', {isOpen: false});
                    },
                    onExited: () => {
                        componentRenderer.destroy('deleteTagDialog');
                    }
                    }
                );
            }}
        />
    );
};

DeleteTagAction.propTypes = {
    rowData: PropTypes.object
};

export default DeleteTagAction;
