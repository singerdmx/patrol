class CheckPoint < ActiveRecord::Base
  has_many :check_managers, dependent: :destroy

  belongs_to :asset

end
