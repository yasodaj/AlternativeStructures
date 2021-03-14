options(scipen=999)
data <- read.table("E:\\TestDavidSolutions\\newGridSize\\Toy0\\toy0_candl_swipl.txt",
                   header = F)

test<- data[,c("V1", "V59", "V61", "V63")]

write.table(test$V1, "E:\\TestDavidSolutions\\newGridSize\\Toy0\\toy0_14X14.txt",
            sep = "\n", row.names = F, col.names = F, quote = F)

#process the fitness column
test$V63 <- lapply(test$V63, function(x) gsub("fitness=","",x))

#process the area column
test$V61 <- lapply(test$V61, function(x) gsub("area=","",x))

#process the area column
test$V59 <- lapply(test$V59, function(x) gsub("leaks=\\[","",x))
test$V59 <- lapply(test$V59, function(x) gsub("\\]","",x))


elems <- unlist( strsplit( as.character(test$V1) , "" ) )
m <- as.data.frame(matrix( elems , ncol = 14 , byrow = TRUE ), stringsAsFactors = F)

elems <- unlist( strsplit( as.character(test$V59) , "," ) )
m1 <- as.data.frame(matrix( elems , ncol = 3 , byrow = TRUE ), stringsAsFactors = F)


final<- cbind(m,m1)

area <- as.numeric(unlist(test$V61))

final<- cbind(final, as.data.frame(area))

fit <- as.numeric(unlist(test$V63))
final<- cbind(final, as.data.frame(fit))

colnames(final)<- c("xI","yI","XN0","yN0","xF","yF","xN1","yN1","xN2","yN2","xF1","yF1","xF2","yF2","Short","Medium","Long","Area","Fitness")


library(plyr)

for(i in 1:14){
  final[,i] <- revalue(final[,i], c("a"="10"))
  final[,i] <- revalue(final[,i], c("b"="11"))
  final[,i] <- revalue(final[,i], c("c"="12"))
  final[,i] <- revalue(final[,i], c("d"="13"))
  final[,i]<-as.numeric(final[,i])
}

for(i in 15:17){
  final[,i]<-as.numeric(final[,i])
}

ID<- c(1:nrow(final))

final<-cbind(ID,final)

write.table(final,"E:\\TestDavidSolutions\\newGridSize\\Toy0\\toy0_matlabMaster.csv",
            sep = ",", row.names = F, col.names = F, quote = F)