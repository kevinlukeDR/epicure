directory.Time = Backbone.Model.extend({
    defaults: {
      maxSeconds: 60
    },
    convertToDoubleDigitString: function (number) {
      if (number == 0) {
        return "00";
      } else if (number < 10) {
        return "0" + number;
      } else {
        return number
      }
    },
    getMaxMinutes: function () {
      var minutes = Math.floor((this.get('maxSeconds') / 60) % 60);
      return (minutes > 0) ? minutes: 0;
    },
    getMinutes: function () {
      var minutes = Math.floor((this.get('seconds') / 60) % 60);

      // return double digits
      return this.convertToDoubleDigitString(minutes);
    },
    getSeconds: function () {
      var seconds = Math.floor(this.get('seconds') % 60);

      // return double digits
      return this.convertToDoubleDigitString(seconds);

    },
    getTimeString: function () { // for example, it returns 01:02
      var s = this.getMinutes() + ":" + this.getSeconds();
      return s;
    },
    startTimer: function () {
      var self = this;
      this.interval = setInterval(function () {
        self.set({seconds: self.get('seconds') + 1});
        self.trigger('timeUpdated', self.getTimeString())
      }, 1000)
    },
    stopTimer: function () {
      clearInterval(this.interval);
    },
    resetTimer: function () {
      this.set({seconds: 0});
    },
    getPercentage: function () {
      var percent = this.get('seconds') / this.get('maxSeconds');
      var percentString = Math.round(percent * 100) + "%";
      return percentString;
    },
    getPercentageFloat: function () {
      var percent = this.get('seconds') / this.get('maxSeconds');
      return percent;
    }
});
