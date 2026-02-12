import {
    ActionsCell,
    NameCell,
    ViewUsageCell
} from '~/content/cells';

export const tagManagerColumns = [
    {
        Header: 'Tag',
        id: 'name',
        accessor: row => row.name.value,
        Cell: NameCell
    },
    {
        Header: 'Usages',
        accessor: 'usagesCount',
        Cell: ViewUsageCell,
        customWidth: '120px'
    },
    {
        Header: 'Actions',
        Cell: ActionsCell,
        customWidth: '240px'
    }
];
