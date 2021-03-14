setwd("D:/BellosData/Test")

install.packages("cba")
library(cba)

votes = read.table("goodsplit_gen_000_jaccardDist.csv",sep = ",", header = T)


Un1 <-  as.character(unique(unlist(votes[1:2])))
votes[1:2] <- lapply(votes[1:2], factor, levels = Un1)
distObj = xtabs(JaccardDist ~ Model1 + Model2, data = votes)

x = as.dist(distObj)
m = rockLink(x, beta = 0.5)


x <- as.dummy(votes[-17])
rc <- rockCluster(x, n=2, theta=0.73, debug=TRUE)
print(rc)
rf <- fitted(rc)



