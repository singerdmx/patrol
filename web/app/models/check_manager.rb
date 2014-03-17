class CheckManager < ActiveRecord::Base
  belongs_to :check_route
  belongs_to :check_point


end
