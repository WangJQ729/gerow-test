##       知客脚本适配
        修改配置文件
        "refresh_count_ttl": 1,        知客分群刷新时间
        "etl_bind_zhike_event":true    是否同步到click house     
##       催单适配
        https://doc.xiaoduoai.com/pages/viewpage.action?pageId=271096997
###     开启trace
#####   修改reminder-stat
        "reminder_trace": {
          "Addrs": ["mongo-newreminder-pri:27017","mongo-newreminder-sec:27017"],
          "Username": "xdmp",
          "Password": "20E6QK8V",
          "Timeout": 10,
          "app_name": "reminder-tb",
          "Mode": 5
        }
#####   修改reminder-stat.conf        
        "record_trace_limit":false
#####   重启reminder-stat
        cd /root/envctl-conf/k8s/test-csjb/configmap/conf-reminder-stat-tb
        kubectl delete configmap conf-reminder-stat-tb -n test-csjb
        kubectl create configmap conf-reminder-stat-tb --from-file=./reminder-stat.conf -n test-csjb
        kubectl -n test-csjb rollout restart deploy reminder-stat-tb-deploy
#####   修改reminder
        cd /root/envctl-conf/k8s/test-csjb/configmap/conf-reminder-tb
#####   在reminder.conf中添加/修改trace信息
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
        kubectl create configmap conf-reminder-tb --from-file=./reminder.conf --from-file=./reminder.conf --from-file=./tb.rmd.json -n test-csjb
        kubectl -n test-csjb rollout restart deploy reminder-tb-deploy  
        