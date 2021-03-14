
L1 <- c("1", "1", "1", "2", "2", "3")
L2 <- c("1", "2", "3", "2", "3", "3")
D <- c(0, 0.408, 1.633,0,1.581,0)
df<-data.frame(L1,L2,D)
  
  
dfr <- reshape(df, direction="wide", idvar="L2", timevar="L1")
dfr

d <- as.dist(dfr[, -1])
d

h <- hclust(d)
plot(h)
