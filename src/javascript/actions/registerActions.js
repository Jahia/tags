import {registry} from '@jahia/ui-extender';
import DeleteTagAction from '~/actions/deleteTag/DeleteTagAction';
import EditTagAction from '~/actions/editTag/EditTagAction';
import TagUsagesAction from '~/actions/tagUsages/TagUsagesAction';

export function registerActions() {
    registry.add('action', 'tagUsages', {
        component: TagUsagesAction
    });

    registry.add('action', 'editTags', {
        targets: ['tagsManagerTableActions:1.0'],
        component: EditTagAction
    });

    registry.add('action', 'deleteTags', {
        targets: ['tagsManagerTableActions:1.1'],
        component: DeleteTagAction
    });
}
