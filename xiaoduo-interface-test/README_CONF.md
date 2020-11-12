##      催单脚本适配
        https://doc.xiaoduoai.com/pages/viewpage.action?pageId=271096997
####   1、修改reminder-stat（淘宝和淘动力都要修改）添加以下mongo配置
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
        cd /root/envctl-conf/k8s/test-csjb/configmap/conf-reminder-stat-tb
        kubectl delete configmap conf-reminder-stat-tb -n test-csjb
        kubectl create configmap conf-reminder-stat-tb --from-file=./reminder-stat.conf -n test-csjb
        kubectl -n test-csjb rollout restart deploy reminder-stat-tb-deploy
####   2、修改reminder（淘宝和淘动力都要修改），添加reminder_trace的目录
        cd /root/envctl-conf/k8s/test-csjb/configmap/conf-reminder-tb
#####   reminder.conf
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