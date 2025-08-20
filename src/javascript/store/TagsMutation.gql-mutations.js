import gql from 'graphql-tag';

/**
 * Adding only as a reference for performing mutations on tags TBD but not sure if scalable.
 * Old code references usage of TaggingService as an alternative.
 */

export const RENAME_TAGS = gql`
    mutation renameTags($pathsOrIds: [String]!, $oldTag: String!, $newTag: String!) {
        jcr {
            mutateNodes(pathsOrIds:$pathsOrIds) {
                mutateProperty(name:"j:tagList") {
                    addValue(type:STRING, value:$newTag)
                    removeValue(type:STRING, value: $oldTag)
                }
            }
        }
    }
`;

export const DELETE_TAGS = gql`
    mutation deleteTags($pathsOrIds: [String]!, $oldTag: String!) {
        jcr {
            mutateNodes(pathsOrIds:$pathsOrIds) {
                mutateProperty(name:"j:tagList") {
                    removeValue(type:STRING, value: $oldTag)
                }
            }
        }
    }
`;
