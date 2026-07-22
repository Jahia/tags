import {useQuery} from '@apollo/client';
import {useTagsManagerContext} from './TagsManager.context';
import {GET_TAGS_QUERY} from './TagsQuery.gql-queries';

const useTagsData = () => {
    const {siteInfo, workspace} = useTagsManagerContext();
    const {data: queryData, loading, error} = useQuery(GET_TAGS_QUERY, {
        variables: {
            workspace: workspace.gqlType,
            sitePath: siteInfo.path
        },
        notifyOnNetworkStatusChange: true,
        fetchPolicy: 'cache-and-network'
    });

    if (loading) {
        return {loading, error};
    }

    /**
     * Transforms node data containing tags into a list of tags with their node references.
     */
    const updateData = () => {
        if (!queryData) {
            return [];
        }

        const tags = {}; // Map of tag names to node references
        const insertTag = (tag, node) => {
            (tags[tag] = tags[tag] || []).push(node);
        };

        queryData?.jcr.nodesByCriteria.nodes.forEach(node => {
            node.tagList.values.forEach(tag => insertTag(tag, node));
        });
        return Object.keys(tags).map(tag => ({
            name: {value: tag},
            usagesCount: tags[tag].length,
            usages: tags[tag]
        }));
    };

    const newData = updateData();
    return {data: newData, loading, error};
};

export default useTagsData;
