<template>
    <div class="tags">
        <el-tag
            v-for="(tag,index) in tags"
                :key="tag.name"
                :closable="tag.name!=='shouye'"
                :effect="route.name===tag.name?'dark':'plain'"
                @click="handleMenu(tag)"
                @close="handleClose(tag,index)"
        >
            {{ tag.label }}
        </el-tag>
    </div>
</template>

<script setup>
import { computed, ref} from 'vue'
import { useRoute, useRouter } from 'vue-router';
import {useAllDataStore} from '@/stores'
    const store=useAllDataStore()
    const tags=computed(()=>store.state.tags)
    const route=useRoute()
    const router=useRouter()
    //跳转_fun
    const handleMenu=(tag)=>{
        router.push({name:tag.name})
        store.selectMenu(tag)
    }
    //关闭_fun
    const handleClose=(tag,index)=>{
        store.updateMenu(tag)
        if(tag.name!==route.name) return;
        if(index===store.state.tags.length){
            store.selectMenu(tags.value[index-1])
            router.push({name:tags.value[index-1].name})
        }else{
            store.selectMenu(tags.value[index])
            router.push({name:tags.value[index-1].name})
        }
    }

</script>
<style lang="less" scoped>
.tags{
    margin: 20px 0 0 20px;
}
.el-tag{
    margin-right: 10px;
}
</style>