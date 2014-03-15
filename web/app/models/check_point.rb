class CheckPoint < ActiveRecord::Base
  belongs_to :asset
  has_many :check_managers, dependent: :destroy

end
