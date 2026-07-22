import gql from 'graphql-tag';

export const GET_TAGS_QUERY = gql`
    query getTags($sitePath: String!, $workspace: Workspace!, $lang: String = "en", $cursor: String) {
        jcr(workspace: $workspace) {
            nodesByCriteria(first:100, after: $cursor, criteria: {
                nodeType:"jmix:tagged",
                paths: [$sitePath],
                nodeConstraint: {property:"j:tagList", exists: true}
            }) {
                nodes {
                    path
                    uuid
                    workspace
                    name
                    displayName(language: $lang)
                    tagList: property(name:"j:tagList") {
                        values
                    }
                }
                pageInfo {
                    hasNextPage
                    nodesCount
                    totalCount
                    endCursor
                }

            }
        }
    }
`;
