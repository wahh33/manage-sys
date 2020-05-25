import {typeText} from '@/utils/typeText.js';
const fileMethods=function(url){
  return {
    setFile(file) {
      let self = this,
        type = file.name.slice(file.name.lastIndexOf(".")),
        size = file.size,
        sizeUnit = self.fileMaxSize.unit.toLowerCase(),
        maxSize = self.fileMaxSize.value * (sizeUnit === 'mb' ? 1024 * 1024 : sizeUnit === 'kb' ? 1024 : 1);
      if (self.fileTypeArr.every(o => o !== type)) {
        self.handleFormatError();
        self.file = null;
      } else if (size > maxSize) {
        self.handleMaxSize();
        self.file = null;
      } else self.file = file;
      return false;
    },
    saveFile() {
      let self = this,
        formFile = new FormData();
      formFile.append('file', self.file);
      return self.$http({
        url: url,
        data: formFile,
        method: 'post',
      });
    },
    handleFormatError() {
      let self = this;
      self.$Notice.warning({
        title: '文件格式不正确',
        desc: '文件格式不正确, 请选择 jpg，jpeg，png 的格式',
      });
    },
    handleMaxSize() {
      let self = this;
      self.$Notice.warning({
        title: '文件大小过大',
        desc: '文件大小过大, 请选择 不大于2M 的文件',
      });
    }
  };
};
const fileDatas=function(){
  return {
    file: null,
    fileModal: false,
    fileSrc: '',
    fileMaxSize: typeText.fileMaxSize,
    fileTypeArr: typeText.fileTypeArr,
  };
};
export {fileMethods,fileDatas};
