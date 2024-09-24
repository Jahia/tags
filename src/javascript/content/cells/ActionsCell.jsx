import React from 'react';
import {Button, Delete, Edit} from '@jahia/moonstone';

export const ActionsCell = () => {
    return (
        <>
            <Button variant="ghost" size="big" icon={<Edit/>}/>
            <Button variant="ghost" size="big" icon={<Delete/>}/>
        </>
    );
};
