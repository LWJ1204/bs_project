import {useAllDataStore} from "@/stores"
import { computed } from "vue";
export default function(){
    const store=useAllDataStore();
    function changeCollapse(){
       store.isCollapse=!store.isCollapse
    }
    return {changeCollapse}
}