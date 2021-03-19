##      催单脚本适配

        https://doc.xiaoduoai.com/pages/viewpage.action?pageId=271096997

####   1、修改reminder-stat

        kubectl -n test-tygj edit cm conf-reminder-stat-tb 添加mongo信息（有就修改，没有就添加）
        
        1、加上reminder_trace的mongo存储信息
        
            "reminder_trace": {
              "Addrs": ["mongo-newreminder-pri:27017","mongo-newreminder-sec:27017"],
              "Username": "xdmp",
              "Password": "20E6QK8V",
              "Timeout": 10,
              "app_name": "reminder-tb",
              "Mode": 5
            }
            
#####   reminder-stat.conf        
        "record_trace_limit":false
#####   重启reminder-stat
        kubectl -n test-tygj rollout restart deployment reminder-stat-tb-deploy
####   2、修改reminder（淘宝和淘动力都要修改），添加reminder_trace的目录
        kubectl -n test-tygj edit cm conf-reminder-tb （添加trace信息（有就修改，没有就添加））
#####   reminder.conf
        加上：
     ·   1、
            "run_mode":"test"
        2、
            "reminder_trace": {
              "Addrs": [
                "mongo-newreminder-pri:27017",
                "mongo-newreminder-sec:27017"
              ],
              "Username": "xdmp",
              "Password": "20E6QK8V",
              "AppName": "reminder-tb",
              "Mode": 5
            }
#####   重启reminder服务
        kubectl delete configmap conf-reminder-tb -n test-csjb