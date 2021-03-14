setwd("D:/BellosData/Goodbadsplit_31042017")

rawFiles = list.files(pattern = "^good.*.csv$")

for(y in 1:length(rawFiles)){
  currentFile = rawFiles[y]
  data = read.csv(currentFile)
  cdata = data
  cdata$ModelName <- NULL
  
  fileName = paste(substr(currentFile,1,nchar(currentFile)-4),"_jaccardDist.csv",sep = "")
  
  jaccard_data = data.frame()
  
  for(i in 1:nrow(data)){
    model1 = substr(data[i,1],9,14)
    model1_a = t(cdata[i,])
    
    v = i + 1
    
    if(i != nrow(data)){
      
      for(x in v:nrow(data)){
        
        model2 = substr(data[x,1],9,14)
        model2_b = t(cdata[x,])
        
        m11_d = model1_a + model2_b
        m11 = length(subset(m11_d, m11_d[,1] == 2))
        
        m10_d = model1_a - model2_b
        m10 = length(subset(m10_d, m10_d[,1] == 1))
        
        #m01_d = model1_a - model2_b
        m01 = length(subset(m10_d, m10_d[,1] == (-1)))
        
        jaccard = m11/(m10+m01+m11)
        
        jaccard_data = rbind(jaccard_data, data.frame(Model1 = model1,
                                                      Model2 = model2,
                                                      JaccardDist = jaccard))
        
      }
      
    }
    
  }
  
  write.csv(jaccard_data, fileName, quote = F, row.names = F)
  jaccard_data <- NULL
  
}


