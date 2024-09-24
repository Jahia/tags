import React from 'react';
import {useTranslation} from 'react-i18next';
import {Button} from '@jahia/moonstone';
import PropTypes from 'prop-types';

export const ViewUsageCell = ({row}) => {
    const {t} = useTranslation('tags');
    const {usagesCount} = row.values;
    return (
        <Button label={t('label.viewUsages.cellLabel', {usagesCount})}/>
    );
};

ViewUsageCell.propTypes = {
    row: PropTypes.object.isRequired
};
