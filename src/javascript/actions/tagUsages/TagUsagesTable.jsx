import React, {useMemo, useState} from 'react';
import {Table, TableBody, TableBodyCell, TableHead, TableHeadCell, TablePagination, TableRow} from '@jahia/moonstone';
import {useTable} from 'react-table';
import PropTypes from 'prop-types';

const TagUsagesTable = ({rowData}) => {
    const [rowsPerPage, setRowsPerPage] = useState(5);
    const [currentPage, setCurrentPage] = useState(1);
    const data = useMemo(
        () => rowData.slice((currentPage - 1) * rowsPerPage, Math.min(rowData.length, currentPage * rowsPerPage)),
        [rowData, currentPage, rowsPerPage]);

    const columns = useMemo(() => [
        {
            Header: 'Name',
            accessor: 'displayName',
            cmRole: 'table-tag-usages-cell-name'
        },
        {
            Header: 'Location',
            accessor: 'path',
            cmRole: 'table-tag-usages-cell-location'
        }
    ], []);

    const {
        getTableProps,
        getTableBodyProps,
        headerGroups,
        rows,
        prepareRow
    } = useTable({data, columns});

    return (
        <>
            <Table {...getTableProps()}>
                <TableHead>
                    {headerGroups.map(headerGroup => (
                        // A key is included in headerGroup.getHeaderGroupProps
                        // eslint-disable-next-line react/jsx-key
                        <TableRow {...headerGroup.getHeaderGroupProps()}>
                            {headerGroup.headers.map(column => (
                                // A key is included in column.getHeaderProps
                                // eslint-disable-next-line react/jsx-key
                                <TableHeadCell {...column.getHeaderProps()} width={column.customWidth}>
                                    {column.render('Header')}
                                </TableHeadCell>
                            ))}
                        </TableRow>
                    ))}
                </TableHead>
                <TableBody {...getTableBodyProps()}>
                    {rows.map(row => {
                        prepareRow(row);
                        return (
                            // A key is included in row.getRowProps
                            // eslint-disable-next-line react/jsx-key
                            <TableRow {...row.getRowProps()}>
                                {row.cells.map(cell => (
                                    // A key is included in cell.getCellProps
                                    // eslint-disable-next-line react/jsx-key
                                    <TableBodyCell {...cell.getCellProps()}
                                                   width={cell.column.customWidth}
                                                   data-cm-role={cell.column.cmRole}
                                    >
                                        {cell.render('Cell')}
                                    </TableBodyCell>
                                ))}
                            </TableRow>
                        );
                    })}
                </TableBody>
            </Table>
            <TablePagination
                currentPage={currentPage}
                totalNumberOfRows={rowData.length}
                rowsPerPage={rowsPerPage}
                onRowsPerPageChange={prevRowsPerPage => setRowsPerPage(prevRowsPerPage)}
                onPageChange={page => setCurrentPage(page)}
            />
        </>
    );
};

TagUsagesTable.propTypes = {
    rowData: PropTypes.arrayOf(PropTypes.shape({
        displayName: PropTypes.string,
        path: PropTypes.string
    }))
};

export default TagUsagesTable;
