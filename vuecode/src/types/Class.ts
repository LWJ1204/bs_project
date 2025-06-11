//限制class对象的具体属性
export interface ClassInter{
    classId:string,//课程id
    className:string,//课程名称
    classRoom:string,//课程上课教室
    classHours:number,//学时
    classWeeks:string,//上课周数
    classFinish:string,//结课形式
    classTeacher:string,//上课教师
    classValue:DoubleRange,//学分
    classDate:string,//上课节数
}

export type Clases=Array<ClassInter>