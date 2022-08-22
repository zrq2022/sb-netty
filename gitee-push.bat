@echo off

@rem 推送到 gitee 远程仓库

@rem 删除远程仓库关联
call git remote rm origin
@rem 添加远程仓库
call git remote add origin https://gitee.com/zrq5462/sb-netty.git
@rem 推送到远程
call git push -u origin "main"