library(ggplot2)
library(reshape2)

final.df <- data.frame()
location = "D:\\SA\\Toy0\\Toy0_SA\\Iterations_5000\\"

for(x in 1:200){

  bestSolFile = paste0(location,"Run",x,"\\BestSolutionsLog.txt")
  
  d = read.table(bestSolFile, header = T, stringsAsFactors = F, 
                 row.names = NULL)
  temp <- d[match(min(d$Elitef), d$Elitef), c("Iteration", "Elitef")]
  Run = x
  
  final.df <- rbind(final.df, data.frame(Run,temp[1],temp[2]))
  
  
  # colnames(d) <- c("Iteration" ,"Fit","Elitef", "Elitex",
  #                  "InitialSolution", "NewSolution", "Temperature")
  # 
  
  
  #create the string with fitness values
  # fitnessValues = unique(d$Elitef)
  # str = paste0("Unique Elitef values (",length(fitnessValues),"): ")
  # for(i in 1:length(fitnessValues)){
  #   
  #   str = paste0(str,fitnessValues[i], " ") 
  #   
  # }
  
  # mdata <- melt(d[,c(1:3)], id=c("Iteration")) 
  
  
  #create plot
  # p <- ggplot(data=mdata, aes(x=Iteration , y=value, group = variable)) +
  #   geom_line(aes(color=variable))+
  #   geom_point(aes(color=variable))+
  #   ggtitle(paste0("Toy Simulated Annealing Run ",x)) +
  #   xlab(paste0("Iteration \n",str)) +
  #   ylab("Fitness") +
  #   scale_color_manual(values=c("#999999", "#CC0066", "#56B4E9"))
  # 
  # pName = paste0("E:\\SA\\Toy\\NewGridSize_WithAlternativeSolutions\\RunStats\\ContigencyPlots\\Run",x,".jpg")
  # 
  # ggsave(pName, plot=p, height = 6, width = 10, units = "in")
  
  # str<-NULL
  # d<-NULL
  # mdata <- NULL
}

#Elitex iteration summary

p2 <- ggplot(data=final.df, aes(x=Run , y=Iteration)) +
  geom_point(colour = "blue", size = 1.5)+
  xlab("Run") +
  ylab("Best fitness - Iteration")

pName = paste0(location, "RunStats\\ContigencyPlots\\ElitexSpread.jpg")

ggsave(pName, plot=p2, height = 6, width = 10, units = "in")



final.df$group = cut(final.df$Iteration,c(0,500,1000,1500,2000, 2500, 3000, 3500, 4000, 4500, 5000))
levels(final.df$group) = c("0-500","501-1000","1001-1500","1501-2000","2001-2500",
                       "2501-3000", "3001-3500", "3501-4000", "4001-4500",
                     "4501-5000")
t <- as.data.frame(table(final.df$group))

ggplot(t, aes(x=Var1, y=Freq)) + 
  geom_bar(stat = "identity")






##hill climber iterations 


dx <- read.table("D:\\40RunSummary_Toy1.txt", header = T, sep = ",", stringsAsFactors = F)
dx <- dx[order(dx$Run),] 
p3 <- ggplot(data=dx, aes(x=Run , y=firstObservedAt)) +
  geom_point(colour = "blue", size = 1.5)+
  xlab("Run") +
  ylab("Best fitness - Iteration")
pName = paste0("D:\\", "HillClimb_Toy1_ElitexSpread.jpg")

ggsave(pName, plot=p3, height = 6, width = 10, units = "in")

p6 <- ggplot(data=dx, aes(x=lowestFit , y=firstObservedAt)) +
  geom_point(colour = "blue", size = 1.5)+
  xlab("Lowest fitness reached") +
  ylab("Iteration lowest fitness first reached")

pName = paste0("D:\\", "HillClimb_Toy1_FI.jpg")

ggsave(pName, plot=p6, height = 6, width = 10, units = "in")

dx$group = cut(dx$firstObservedAt,c(0, 2500, 5000, 7500, 10000 , 12500, 15000,
                                    17500,
                                    20000, 30000,  
                                    40000, 100000))
levels(dx$group) = c("0-2500","2501-5000", "5001-7500", "7501-10000", 
                     "10001-12500","12501-15000", "15001-17500", "17501-20000", 
                     "20001-30000", "30001-40000", "40001-50000",
                     "> 50001")

max(dx$firstObservedAt)

tdx <- as.data.frame(table(dx$group))

p4 <- ggplot(tdx, aes(x=Var1, y=Freq)) + 
  geom_bar(stat = "identity", fill = "violetred4")+
  xlab("Best fitness range") +
  ylab("Frequency") +
  theme(axis.text.x = element_text(angle = 90)) 

pName = paste0("D:\\", "HillClimb_Toy1_ElitexSpread_bar2.jpg")

ggsave(pName, plot=p4, height = 6, width = 10, units = "in")


