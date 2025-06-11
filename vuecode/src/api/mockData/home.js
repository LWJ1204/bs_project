
export default{
    getTableData:()=>{
        return{
            code:200,
            data:{
                TableData:[
                    {
                        classId: "A101",
                        className: "高等数学",
                        classRoom: "博三A101",
                        classWeeks: "1-16",
                        classTeacher:"张三",
                        classDate: "1-2"
                    },
                    {
                        classId: "A202",
                        className: "大学物理",
                        classRoom: "博三A202",
                        classWeeks: "2-18",
                        classTeacher:"李四",
                        classDate: "5-6"
                    },
                    {
                        classId: "A303",
                        className: "大学英语",
                        classRoom: "博三A303",
                        classWeeks: "4-19",
                        classTeacher:"王五",
                        classDate: "7-8"
                    },
                ],
            },
        }
    },

    getCountData:()=>{
        return{
            code:200,
            data:{
                CountData:[
                    {
                        name:"今日课程数目",
                        value:10,
                        color:"#5ab1ef",
                        icon:"GoodsFilled",
                    },
                    {
                        name:"今日请假人数",
                        value:20,
                        color:"#2ec7cb",
                        icon:"SuccessFilled",
                    },
                    {
                        name:"今日签到人数",
                        value:50,
                        color:"#ffb980",
                        icon:"StarFilled",
                    },
                    {
                        name:"今日缺勤人数",
                        value:10,
                        icon:"GoodsFilled",
                        color:"#5ab1ef",
                    }
                ],
            },
        }
    }
}