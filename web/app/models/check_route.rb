class CheckRoute < ActiveRecord::Base
  has_many :check_managers, dependent: :destroy
end
