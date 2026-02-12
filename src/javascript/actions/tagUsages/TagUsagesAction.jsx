import React, {useContext} from 'react';
import {ComponentRendererContext} from '@jahia/ui-extender';
import {ButtonRenderer} from '@jahia/jcontent';
import TagUsagesDialog from '~/actions/tagUsages/TagUsagesDialog';
import PropTypes from 'prop-types';

const TagUsagesAction = ({rowData, ...others}) => {
    const componentRenderer = useContext(ComponentRendererContext);
    const usagesCount = rowData.usagesCount;
    return (
        <ButtonRenderer
            {...others}
            buttonLabelNamespace="tags"
            buttonLabel="label.viewUsages.cellLabel"
            buttonLabelParams={{count: usagesCount}}
            onClick={() => {
                componentRenderer.render('tagUsagesDialog', TagUsagesDialog, {
                        rowData,
                        isOpen: true,
                        onClose: () => {
                            componentRenderer.setProperties('tagUsagesDialog', {isOpen: false});
                        },
                        onExited: () => {
                            componentRenderer.destroy('tagUsagesDialog');
                        }
                    }
                );
            }}
        />
    );
};

TagUsagesAction.propTypes = {
    rowData: {
        usagesCount: PropTypes.number
    }
};

export default TagUsagesAction;
