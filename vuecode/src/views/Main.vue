<script setup>
// import { ElContainer, ElHeader, ElMain } from 'element-plus';
import CommonAside from '@/components/CommonAside.vue';
import CommonHeader from "@/components/CommonHeader.vue";
import CommonTab from '@/components/CommonTab.vue';

import { RouterView } from 'vue-router';
</script>

<template>
    <div class="common-layout">
        <el-container class="lay-container">
            <CommonAside/>
            <el-container>
                <el-header class="el-header">
                    <CommonHeader/>
                </el-header>
                <CommonTab/>
                <el-main class="right-main">
                    <RouterView></RouterView>
                </el-main>
            </el-container>
        </el-container>
    </div>
</template>

<style scoped lang="less">
.common-layout, .lay-container {
    height: 100%;
}
.el-header {
    background-color: #333;
}
</style>