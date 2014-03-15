class CheckManager < ActiveRecord::Base
  belongs_to :check_path
  belongs_to :check_point
end
